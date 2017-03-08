exports.handler = function(event, context) {
    var data = "Hello "+event['name']
    console.log(data);
    console.log('Received Event:', JSON.stringify(event, null, 2));
    context.succeed(data);
};
