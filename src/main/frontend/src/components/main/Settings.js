import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../../css/OauthUserInfoPage.css';

axios.defaults.withCredentials = true;

const Settings = () => {
    const accessToken = localStorage.getItem('accessToken');
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [birth, setBirth] = useState('');
    const [gender, setGender] = useState('');
    const [age, setAge] = useState('');
    const [location, setLocation] = useState('');
    const [imageUrl, setImageUrl] = useState('');
    const [newImage, setNewImage] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get('http://localhost:8080/user/info', {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });

                const { email, name, birth, gender, age, location, imageUrl } = response.data;
                setEmail(email || '');
                setName(name || '');
                setBirth(birth || '');
                setGender(gender || '');
                setAge(age || '');
                setLocation(location || '');
                setImageUrl(imageUrl || '');

                if (imageUrl && imageUrl.startsWith('/saveimages/')) {
                    await fetchProfileImage();
                } else {
                    setImageUrl(imageUrl || '');
                }

            } catch (error) {
                setError(error.response?.data || "사용자 정보를 불러오는 데 실패했습니다: 서버와의 통신에 실패했습니다.");
                console.error("사용자 정보 불러오기 실패:", error.response ? error.response.data : error.message);
            }
        };

        fetchUserInfo();
    }, []);

    const fetchProfileImage = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/userImage/upload`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                },
                responseType: 'blob', // 이미지를 바이너리 형식으로 받아옴
            });

            const imageBlob = response.data;
            const imageUrl = URL.createObjectURL(imageBlob);
            setImageUrl(imageUrl);
        } catch (error) {
            setError(error.response?.data || "이미지 불러오기 실패: 서버와의 통신에 실패했습니다.");
            console.error("이미지 불러오기 실패:", error.response ? error.response.data : error.message);
        }
    };

    const handleUserInfo = async () => {
        try {
            await axios.post('http://localhost:8080/user/info', {
                email,
                name,
                birth,
                gender,
                age,
                location
            }, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });

            alert("정보 수정 성공!");

            if (newImage) {
                await handleImageUpload(); // 이미지가 있을 경우에만 업로드
            }

            navigate("/main/map");
        } catch (error) {
            setError(error.response?.data || "정보 수정 실패: 서버와의 통신에 실패했습니다.");
            console.error("정보 수정 실패:", error.response ? error.response.data : error.message);
        }
    };

    const handleImageUpload = async () => {
        if (!newImage) return;

        const formData = new FormData();
        formData.append('imageFile', newImage);

        try {
            await axios.post('http://localhost:8080/userImage/update', formData, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'multipart/form-data'
                }
            });

            alert("이미지 업로드 성공!");
            await fetchProfileImage();
        } catch (error) {
            setError(error.response?.data || "이미지 업로드 실패: 서버와의 통신에 실패했습니다.");
            console.error("이미지 업로드 실패:", error.response ? error.response.data : error.message);
        }
    };

    const handleImageChange = (event) => {
        const file = event.target.files[0];
        setNewImage(file);
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setImageUrl(imageUrl); // 파일을 즉시 미리보기
        }
    };

    return (
        <div className="userinfo-container">
            <h2>개인정보</h2>
            <div className="userinfo-form">
                {error && <div className="error-message">{error}</div>}
                <div className="profile-image">
                    {imageUrl && <img src={imageUrl} alt="Profile" className="profile-image" />}
                </div>
                <div className="userinfo-field">
                    <label>이메일</label>
                    <input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>이름</label>
                    <input
                        type="text"
                        placeholder="이름"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>생년월일</label>
                    <input
                        type="date"
                        placeholder="생년월일"
                        value={birth}
                        onChange={(e) => setBirth(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>성별</label>
                    <select
                        value={gender}
                        onChange={(e) => setGender(e.target.value)}
                        className="userinfo-input"
                    >
                        <option value="">성별</option>
                        <option value="M">남성</option>
                        <option value="F">여성</option>
                    </select>
                </div>
                <div className="userinfo-field">
                    <label>나이</label>
                    <input
                        type="number"
                        placeholder="나이"
                        value={age}
                        onChange={(e) => setAge(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>사는 지역</label>
                    <input
                        type="text"
                        placeholder="사는 지역"
                        value={location}
                        onChange={(e) => setLocation(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>프로필 이미지</label>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        className="userinfo-input"
                    />
                </div>
                <button onClick={handleUserInfo} className="userinfo-button">
                    설정 완료
                </button>
            </div>
        </div>
    );
};

export default Settings;