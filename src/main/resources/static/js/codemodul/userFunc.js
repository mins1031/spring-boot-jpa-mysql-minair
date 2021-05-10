console.log("userFunc");
function userInfo(accessToken,refreshToken) {
    $.ajax({
        type : 'get',
        url : '/user/info',
        data : {
            "token":accessToken
        },
        beforeSend: function (xhr){
            xhr.setRequestHeader("Authorization",accessToken);
        },
        statusCode:{
            200 : function (response) {
                //info 페이지로 가는건 단순히 그냥 토큰 값이 만료되지 않고, 권한이있으면
                //$(location).httr?이거였나 이걸로 페이지 전환시키고
                //유저 정보는 info페이지에서 추후에 ajax로 비동기통신으로 화면 갱신시키는 구조로 구현할것.

            }
        }
    });

}