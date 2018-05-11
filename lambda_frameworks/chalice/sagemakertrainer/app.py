from chalice import Chalice
from chalice import BadRequestError

import boto3, json

app = Chalice(app_name='training-scheduler')
sm = boto3.client('sagemaker')

app.debug = True

@app.route('/')
def index():
    return {'training-scheduler': 'v0.1'}

@app.route('/list/{results}')
def list_jobs(results):
    jobs = sm.list_training_jobs(MaxResults=int(results), SortBy="CreationTime", SortOrder="Descending")
    job_names = map(lambda job: [job['TrainingJobName'], job['TrainingJobStatus']],  jobs['TrainingJobSummaries'])
    return {'jobs': list(job_names)}

@app.route('/get/{name}')
def get_job_by_name(name):
    job = sm.describe_training_job(TrainingJobName=name)
    return {'job': str(job)}

@app.route('/train/{name}', methods=['POST'])
def train_job_by_name(name):
    job = sm.describe_training_job(TrainingJobName=name)

    body = app.current_request.json_body
    if 'TrainingJobName' not in body:
        raise BadRequestError('Missing new job name')
    else:
       job['TrainingJobName'] = body['TrainingJobName']
    if 'S3OutputPath' in body:
        job['OutputDataConfig']['S3OutputPath'] = body['S3OutputPath']
    if 'InstanceType' in body:
        job['ResourceConfig']['InstanceType'] = body['InstanceType']
    if 'InstanceCount' in body:
        job['ResourceConfig']['InstanceCount'] = int(body['InstanceCount'])

    if 'VpcConfig' in job:
        resp = sm.create_training_job(
            TrainingJobName=job['TrainingJobName'], AlgorithmSpecification=job['AlgorithmSpecification'], RoleArn=job['RoleArn'],
            InputDataConfig=job['InputDataConfig'], OutputDataConfig=job['OutputDataConfig'],
            ResourceConfig=job['ResourceConfig'], StoppingCondition=job['StoppingCondition'],
            HyperParameters=job['HyperParameters'] if 'HyperParameters' in job else {},
            VpcConfig=job['VpcConfig'],
            Tags=job['Tags'] if 'Tags' in job else [])
    else:
        # Because VpcConfig cannot be empty like HyperParameters or Tags :-/
        resp = sm.create_training_job(
            TrainingJobName=job['TrainingJobName'], AlgorithmSpecification=job['AlgorithmSpecification'], RoleArn=job['RoleArn'],
            InputDataConfig=job['InputDataConfig'], OutputDataConfig=job['OutputDataConfig'],
            ResourceConfig=job['ResourceConfig'], StoppingCondition=job['StoppingCondition'],
            HyperParameters=job['HyperParameters'] if 'HyperParameters' in job else {},
            Tags=job['Tags'] if 'Tags' in job else [])
    return {'ResponseMetadata': resp['ResponseMetadata']}

