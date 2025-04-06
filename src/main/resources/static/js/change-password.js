function CheckNewPassword(){
    const passwordRegex  = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=]).{8,20}$/;
    const password = document.getElementById("new_password").value.trim();

    const message = document.getElementById("check_password_noti");
    if (!password) {
        message.innerText = "새 비밀번호를 입력해주세요.";
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