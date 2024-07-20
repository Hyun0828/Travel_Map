import React from 'react';

function UserProfile() {
    return (
        <div className="user-profile-container">
            <h1>개인정보 입력</h1>
            <form>
                <div>
                    <label>이름</label>
                    <input type="text" />
                </div>
                <div>
                    <label>전화번호</label>
                    <input type="text" />
                </div>
                <div>
                    <label>주소</label>
                    <input type="text" />
                </div>
                <button type="submit">저장</button>
            </form>
        </div>
    );
}

export default UserProfile;
