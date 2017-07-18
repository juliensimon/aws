'use strict';

exports.handler = (event, context, callback) => {
    const request = event.Records[0].cf.request;
    const headers = request.headers;

    if (request.uri !== '/album.jpg') {
        // do not process if this is not an A-B test request
        callback(null, request);
    } else {

        const groupA = 'IronMaiden';
        const groupB = 'Metallica';
        const groupC = 'JudasPriest';
        const groupD = 'Motorhead';
        
        var albumGroup;
        const groupAObject = '/im1.jpg';
        const groupBObject = '/met1.jpg';
        const groupCObject = '/jp1.jpg';
        const groupDObject = '/mo1.jpg';
        
        var r = Math.random();
        if (r < 0.25) {
            request.uri = groupAObject;
            albumGroup = groupA;
        } else if (r < 0.50) {
            request.uri = groupBObject;
            albumGroup = groupB;
        } else if (r < 0.75) {
            request.uri = groupCObject;
            albumGroup = groupC;
        } else {
            request.uri = groupDObject;
            albumGroup = groupD;
        }

        headers['x-album-group'] = albumGroup;

        callback(null, request);
    }
};

