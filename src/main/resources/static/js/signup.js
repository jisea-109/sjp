function SendSecurityCode() {
    const email = document.getElementById("email").value;
    if (!email || email == null) {
        document.getElementById("emailNotification").style.color = "red";
        document.getElementById("emailNotification").innerText = "이메일을 입력해주세요.";
        return;
    }
    fetch("/email/send-code", {
        method: "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ email })
    })
    .then(res => res.json()) 
    .then(data => {
        document.getElementById("emailNotification").style.color = "green";
        document.getElementById("emailNotification").innerText = data.message;
        document.getElementById("verify_code_btn").disabled = false;
        startTimer();
    })
    .catch(err => {
        document.getElementById("emailNotification").style.color = "red";
        document.getElementById("emailNotification").innerText = "인증번호 전송에 실패했습니다.";
        console.error(err);
    });
}
function VerifySecurityCode() {
    const email = document.getElementById("email").value;
    const code = document.getElementById("emailCode").value;
    if (!code || code == null) {
        document.getElementById("emailNotification").style.color = "red";
        document.getElementById("emailNotification").innerText = "인증번호를 입력해주세요.";
        return;
    }
    if (!email || email == null) {
        document.getElementById("emailNotification").style.color = "red";
        document.getElementById("emailNotification").innerText = "이메일을 입력해주세요.";
        return;
    }
    fetch("/email/verify-code", {
        method: "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ email, code })
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("인증 실패");
        }
        return res.text();
    })
    .then(msg => {
        document.getElementById("emailNotification").style.color = "green";
        document.getElementById("emailNotification").innerText = "인증 완료되었습니다.";
        stopTimer();
    })
    .catch(err => {
        document.getElementById("emailNotification").style.color = "red";
        document.getElementById("emailNotification").innerText = "인증 실패. 인증번호를 다시 확인해주세요.";
        console.error(err);
    });
}

function CheckUsername(){
    const usernameRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{7,15}$/;
    const username = document.getElementById("id").value.trim();

    const message = document.getElementById("check_username_noti");
    if (!username) {
        message.innerText = "아이디를 입력해주세요.";
        message.style.color = "black";
        return;
    }
    if (!usernameRegex.test(username)) {
        message.innerText = "아이디는 영문과 숫자를 포함한 7자 이상, 15자 이하이어야 합니다.";
        message.style.color = "red";
        return;
    }
    if (usernameRegex.test(username)) {
        message.innerText = "사용 가능한 아이디입니다.";
        message.style.color = "blue";
    }
    fetch(`/check-username?username=${encodeURIComponent(username)}`)
        .then(response => response.text())
        .then(result => {
            if (result === "EXISTS") {
                message.innerText = "이미 사용중인 아이디입니다.";
                message.style.color = "red";
            } else if (result === "AVAILABLE") {
                message.innerText = "사용 가능한 아이디입니다.";
                message.style.color = "blue";
            } else {
                message.innerText = "알 수 없는 오류가 발생했습니다.";
                message.style.color = "red";
            }
        })
        .catch(error => {
            console.error("중복 체크 에러:", error);
            message.innerText = "서버 오류 발생";
            message.style.color = "red";
        });
}
function CheckPassword(){
    const passwordRegex  = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=]).{8,20}$/;
    const password = document.getElementById("password").value.trim();

    const message = document.getElementById("check_password_noti");
    if (!password) {
        message.innerText = "비밀번호를 입력해주세요.";
        message.style.color = "black";
        return;
    }
    if (!passwordRegex.test(password)) {
        message.innerText = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상, 20자 이하이어야 합니다.";
        message.style.color = "red";
        return;
    }
    if (passwordRegex.test(password)) {
        message.innerText = "알맞은 비밀번호 형식입니다";
        message.style.color = "blue";
    }
}
function CheckEmail(){
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const email = document.getElementById("email").value.trim();

    const message = document.getElementById("checkEmailNoti")
    if (!email) {
        message.innerText = "이메일을 입력해주세요.";
        message.style.color = "black";
        return;
    }
    if (!emailRegex.test(email)) {
        message.innerText = "올바른 이메일 형식을 입력해주세요.";
        message.style.color = "red"
        return;
    }
    fetch(`/check-email?email=${encodeURIComponent(email)}`)
        .then(res=> res.text())
        .then(result => {
            if (result === "EXISTS") {
                message.innerText = "이미 등록되어 있는 이메일입니다."
                message.style.color = "red";
            } else if (result === "AVAILABLE") {
                message.innerText = "사용 가능한 이메일입니다."
                message.style.color = "blue";
            } else {
                message.innerText = "알 수 없는 오류가 발생했습니다.";
                message.style.color = "red";
            }
        })
        .catch(error => {
            console.error("중복 체크 에러:", error);
            message.innerText = "서버 오류 발생";
            message.style.color = "red";
        });
}

let timer;
let codeExpireTime = 300;
function startTimer() {
    clearInterval(timer);
    codeExpireTime = 300;
    timer = setInterval(() => {
        if (codeExpireTime <= 0) {
            clearInterval(timer);
            document.getElementById("timerDisplay").innerText = "인증 시간이 만료되었습니다.";
            document.getElementById("send_code_btn").disabled = false;
            document.getElementById("verify_code_btn").disabled = true;
        } else {
            let minutes = Math.floor(codeExpireTime / 60);
            let seconds = codeExpireTime % 60;
            document.getElementById("timerDisplay").innerText = `남은 시간: ${minutes}분 ${seconds < 10 ? '0' : ''}${seconds}초`;
            codeExpireTime-=1;
        }
    }, 1000);
}

function stopTimer() {
    clearInterval(timer);
}