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
                    result = response.token
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

    function issueRefresh(username){
        var result = null
        var status = 0
        $.ajax({
            type: 'post',
            url: "/api/token/refresh/" + username,
            /*data: {
                "username": username
            },*/
            async: false,
            statusCode: {
                200: function (response) {
                    console.log("200!")
                    status = 200;
                    result = response.token
                },
                400: function (response) {
                    console.log("400!")
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

    return {
        login:login,
        issueRefresh:issueRefresh
    };
})();