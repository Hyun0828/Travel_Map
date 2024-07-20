import React from 'react';

function Signup() {
    return (
        <div className="signup-container">
            <h1>회원가입</h1>
            <form>
                <div>
                    <label>이메일</label>
                    <input type="email"/>
                </div>
                <div>
                    <label>비밀번호</label>
                    <input type="password"/>
                </div>
                <div>
                    <label>비밀번호 확인</label>
                    <input type="password"/>
                </div>
                <div>
                    <label>이름</label>
                    <input type="text"/>
                </div>
                <button type="submit">회원가입</button>
            </form>
        </div>
    );
}

export default Signup;
