// 콘솔에 아래 입력하여 실행 시켜보기
// ajax({
//     url: 'https://jsonplaceholder.typicode.com/users',
//     method: 'get',
//     success: function(data){ console.log(JSON.parse(data.response)); }
// });

(function(win) {
    'use strict';
    win.ajax = (function() {
        const createXHR = (function() {
            if (!XMLHttpRequest) {
                return function() {
                    return new ActiveXObject('Microsoft.XMLHTTP');
                }
            } else {
                return function() {
                    return new XMLHttpRequest();
                }
            }
        })();

        return function(ajax_params) {
            const success = ajax_params.success;
            const async = true;
            const request = createXHR();

            if (!ajax_params.method) {
                return console.error("Method argument shouldn't be Null");
            } else {
                const method = ajax_params.method;
            }

            if (!ajax_params.url) {
                return console.warn("URL argument shouldn't be Null");
            } else {
                const url = ajax_params.url;
            }

            request.open(ajax_params.method, ajax_params.url, async);
            request.send();

            // 비동기 통신을 위한 onreadystatechange 이벤트 리스너 등록
            request.onreadystatechange = function(event) {
                if (request.readyState == 4) {
                    if (200 <= request.status && request.status < 300) {
                        success({
                            'status': request.status,
                            'statusText': request.statusText,
                            'response': request.responseText
                        });
                    } else {
                        if (ajax_params.error) {
                            ajax_params.error({
                                status: request.status,
                                statusText: request.statusText,
                                response: request.reponseText,
                            });
                        }
                    }

                    if (ajax_params.complete) {
                        ajax_params.complete({
                            status: request.status,
                            statusText: request.statusText,
                            response: request.responseText,
                        });
                    }
                }
            }
        }
    })();
})(window);