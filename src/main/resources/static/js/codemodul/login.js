console.log("login...");

var login = (function(){

    function login(loginForm,callback,error){
        console.log("login func start")
        var result = null;
        var status = 0;
        $.ajax({
            type : 'post',
            url : '/api/member/login',
            data : JSON.stringify(loginForm),
            contentType : "application/json; charset=utf-8",
            async:false,
            statusCode: {
                200: function (response) {
                    status = 200;
                    result = response
                },
                400: function (response) {
                    status = 400;
                    result = null;
                }
            }
        })
        return {
            status:status,
            result:result
        }
    }

    function issueRefresh(username,callback,error){

        $.ajax({
            type: 'post',
            url: '/api/token/refresh',
            data: {
                "username": username
            },
            async: false,
            success: function (result, status, xhr) {
                if (callback) {
                    callback(result, xhr)
                }
            },
            error: function (xhr, status, er) {
                if (error) {
                    error(er);
                }
            }
        })
    }

    return {
        login:login,
        issueRefresh:issueRefresh
    };
})();