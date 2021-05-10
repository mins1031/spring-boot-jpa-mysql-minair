function refresh(data) {
    console.log("Try refresh");

    let refreshToken = data


    let state = null;
    let memberId = null;
    let accessToken = null;

    $.ajax({
        method: "POST",
        url: "/reissue",
        data: {
            "refreshToken":refreshToken
        },
        async: false,
        statusCode: {
            200: function (response) {
                console.log("Refresh success!!");
                state = 200;
                accessToken = response;
            },
            401: function (response) {
                console.log("Refresh fail...");
                let message = response;
                state = 401;
            },
            403: function (response) {
                console.log("Refresh valid over!!");
                alert(response);
                state = 403;
            }
        }
    });

    return {
        accessToken: accessToken,
        state : state
    }
}
function getTokenInfo() {
    console.log("Get token info.");
    let accessToken = localStorage.getItem('Authorization');
    let refreshToken = localStorage.getItem('refreshToken');
    let memberId = localStorage.getItem('memberId');


    if (accessToken == null) {
        console.log("Local storage is null.");
        console.log("Get token info from session storage.");
    }

    return {
        accessToken: accessToken,
        refreshToken: refreshToken,
        memberId:memberId
    };
}

function storageClear() {
    console.log("Delete tokens");
    localStorage.removeItem('memberId');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    sessionStorage.removeItem('memberId');
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
}

function ifRefreshFail() {
    alert("Refresh token이 유효하지 않습니다.");
    storageClear();
    $(location).attr('href', '/');
}
