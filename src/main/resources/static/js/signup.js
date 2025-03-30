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
    .then(res => res.text())
    .then(msg => {
        document.getElementById("emailNotification").style.color = "green";
        document.getElementById("emailNotification").innerText = "이메일로 인증번호가 전송되었습니다.";
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
    })
    .catch(err => {
        document.getElementById("emailNotification").style.color = "red";
        document.getElementById("emailNotification").innerText = "인증 실패. 인증번호를 다시 확인해주세요.";
        console.error(err);
    });
}