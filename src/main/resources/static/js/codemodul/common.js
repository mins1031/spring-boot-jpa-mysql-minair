    function tokenCheck() {

        let tokenClass = getTokenInfo()
        let access = tokenClass.accessToken;
        let refresh = tokenClass.refreshToken;
        let username = tokenClass.memberId;


        var result = false;
        if (access != null && refresh != null && username != null){
            result = true;
        }

        return {
            result : result
        }
    }

//토큰 유효기간 체크이므로 엑세스 토큰 넣고 함수 실행함
//컨트롤러에서 200 뱉으면 토큰기간 유효함으로 결과값 true 출력
//401 뱉으면 유효기간 만료임으로 false 출력

    function tokenExpirationCheck(access,refreshToken) {
        let result = null;

        $.ajax({
            method: "get",
            url: "/api/token/tokenExpirationCheck",
            data: {
                "accessToken": access
            },
            async: false,
            statusCode: {
                200: function (response) {
                    console.log("token valid!!");
                    result = null;
                },
                401: function (response) {
                    console.log("token not valid...");
                    //result = false;
                    let reToken = refresh(refreshToken);
                    if (reToken.state === 200) {
                        result = reToken.accessToken;
                    } else if (reToken.state === 401){
                        result = 401;
                    } else if (reToken.state === 403){
                        result = 403;
                    }
                }
            }
        });
        return {
            result: result
        }
    }
    function loginStatus() {

        function loginCheck() {
            var existence = tokenCheck().result;
            console.log("test=" + existence);
            //로컬스토리지에 엑세스,리프레시토큰,memberId값이 있으면 true값이 existence에 반환
            var loginStatus = false;
            //토큰 유무값 리턴변수
            let tokenResult = null;
            if (existence) { //existence가 true면 실행됨.= 2토큰,id값이 있으면 실행.
                tokenResult = tokenExpirationCheck(getTokenInfo().accessToken,
                    getTokenInfo().refreshToken);
                if (tokenResult.result === null) {
                    loginStatus = true;
                    console.log("tokenExpirationCheck 200!")
                } else if (tokenResult.result === 401){
                    loginStatus = 401;
                    localStorage.clear();
                    console.log("로그인 정보 확인불가! 다시 로그인 해주세요.");
                } else if (tokenResult.result === 403){
                    loginStatus = 403;
                    localStorage.clear();
                    console.log("로그인 기한만료! 다시 로그인 해주세요.");
                } else {
                    loginStatus = true;
                    console.log("tokenExpirationCheck 401...")
                    localStorage.setItem("Authorization", tokenResult.result);
                }
            }
            return {
                loginStatus: loginStatus
            }
        }

        var status = loginCheck().loginStatus;
        console.log("status=" + status);

        return{
            status:status
        }
    }

    function adminCheck() {
        var username = localStorage.getItem("memberId");
        console.log("adminCheck= "+username);
        var status;
        $.ajax({
            method : 'get',
            url : "/api/member/checkAdmin/" + username,
            /*data : {
                "username":username
            },*/
            async: false,
            statusCode: {
                200: function (response) {
                   console.log("어드민 계정 인증완료")
                    status = 200;
                },
                403: function (response) {
                    console.log("어드민 계정 인증실패")
                    status = 403;
                }
            }
        })
        return {
            status:status
        }
    }

    function logout(accessToken,refreshToken) {
        var token = refreshToken;
        $.ajax({
            method : 'get',
            url : "/api/member/logout/" + token,
            /*data : {
                "refreshToken":refreshToken
            },*/
            async: false,
            beforeSend: function (xhr){
                xhr.setRequestHeader("Authorization",accessToken);
            },
            statusCode: {
                200: function (response) {
                    localStorage.clear();
                    alert("로그아웃 되었습니다!!!")
                    self.location = "/";
                },
                401: function (response) {
                    alert("일시적 오류입니다. 새로고침후 다시 시도해주세요.")
                }
            }
        })
    }

