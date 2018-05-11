import boto3, os, datetime

def main(event, context):

    training_job_name = os.environ['training_job_name']

    sm = boto3.client('sagemaker')
    job = sm.describe_training_job(TrainingJobName=training_job_name)

    training_name_prefix = os.environ['training_name_prefix']
    training_job_name = training_job_prefix.join(str(datetime.datetime.today()).replace(' ', '-').replace(':', '-').rsplit('.')[0])
    job['ResourceConfig']['InstanceType'] = os.environ['instance_type']
    job['ResourceConfig']['InstanceCount'] = int(os.environ['instance_count'])

    print("Starting training job %s" % training_job_name)

    if 'VpcConfig' in job:
        resp = sm.create_training_job(
            TrainingJobName=training_job_name, AlgorithmSpecification=job['AlgorithmSpecification'], RoleArn=job['RoleArn'],
            InputDataConfig=job['InputDataConfig'], OutputDataConfig=job['OutputDataConfig'],
            ResourceConfig=job['ResourceConfig'], StoppingCondition=job['StoppingCondition'],
            HyperParameters=job['HyperParameters'] if 'HyperParameters' in job else {},
            VpcConfig=job['VpcConfig'],
            Tags=job['Tags'] if 'Tags' in job else [])
    else:
        # Because VpcConfig cannot be empty like HyperParameters or Tags :-/
        resp = sm.create_training_job(
            TrainingJobName=training_job_name, AlgorithmSpecification=job['AlgorithmSpecification'], RoleArn=job['RoleArn'],
            InputDataConfig=job['InputDataConfig'], OutputDataConfig=job['OutputDataConfig'],
            ResourceConfig=job['ResourceConfig'], StoppingCondition=job['StoppingCondition'],
            HyperParameters=job['HyperParameters'] if 'HyperParameters' in job else {},
            Tags=job['Tags'] if 'Tags' in job else [])

    print(resp)
