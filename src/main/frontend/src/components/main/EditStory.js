import React, {useEffect, useState} from "react";
import axios from "axios";
import {Button, List, ListItem, ListItemText, TextField} from "@mui/material";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../../css/EditStory.css';
import {useNavigate, useParams} from "react-router-dom";

axios.defaults.withCredentials = true;

const EditStory = () => {
    const {story_id} = useParams();
    const [title, setTitle] = useState("");
    const [date, setDate] = useState(new Date());
    const [location, setLocation] = useState("");
    const [locationObj, setLocationObj] = useState(null);
    const [content, setContent] = useState("");
    const [prevImages, setPrevImages] = useState([]);
    const [images, setImages] = useState([]);
    const [previewURLs, setPreviewURLs] = useState(['/images/anonymous.png']);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [searchResults, setSearchResults] = useState([]);
    const accessToken = localStorage.getItem('accessToken');
    const navigate = useNavigate();

    /**
     * 네이버 검색 api
     */
    const handleSearch = async (text) => {
        if (text && typeof text === 'string' && text.trim() !== "") {
            try {
                const response = await axios.get("http://localhost:8080/naver/search", {
                    params: {text},
                    headers: {
                        "X-Naver-Client-Id": process.env.REACT_APP_NAVER_CLIENT_ID,
                        "X-Naver-Client-Secret": process.env.REACT_APP_NAVER_CLIENT_SECRET,
                        Authorization: `Bearer ${accessToken}`
                    }
                });
                console.log("Search results:", response.data.items);
                setSearchResults(response.data.items);
            } catch (error) {
                console.error("Error fetching data: ", error);
                setSearchResults([]);
            }
        } else {
            setSearchResults([]);
        }
    };

    /**
     * 이미지 추가 버튼 클릭
     */
    const handleImageChange = (e) => {
        e.preventDefault();
        const files = Array.from(e.target.files);
        const newPreviewURLs = files.map(file => URL.createObjectURL(file));
        setImages(files);
        setPreviewURLs(newPreviewURLs);
        setCurrentImageIndex(0);
    };

    /**
     * 검색 결과 클릭
     */
    const handleResultClick = (item) => {
        if (item) {
            setLocation(item.title.replace(/<\/?b>/g, ""));
            setLocationObj(item);
        }
        setSearchResults([]);
    };

    /**
     * 일기 정보 가져오기
     */
    useEffect(() => {
        const getStory = async () => {
            const {data} = await axios.get(`http://localhost:8080/story?storyId=${story_id}`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                }
            });
            return data;
        }

        const getImages = async () => {
            const response = await axios.get(`http://localhost:8080/storyImages?storyId=${story_id}`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                }
            });
            return response.data;
        };

        getStory().then((result) => {
            setTitle(result.title);
            setContent(result.content);
            setDate(new Date(result.date));
            setLocation(result.place);
            setLocationObj({
                title: result.place,
                roadAddress: result.address
            });
        });
        getImages().then(async imageUrls => {
            console.log(imageUrls);

            setPreviewURLs(imageUrls.length ? imageUrls : ['/images/anonymous.png']);

            // images에는 url이 아닌 blob 객체를 넣어야 한다.
            const imageBlobs = await Promise.all(imageUrls.map(async (url) => {
                const response = await fetch(url);
                const blob = await response.blob();
                const file = new File([blob], url.split('/').pop(), { type: blob.type });
                return file;
            }));

            setImages(imageBlobs);
        });
    }, [])

    /**
     * 수정하기 버튼 클릭
     */
    const handleSubmit = async () => {
        if (!title || !location || !content) {
            alert("모든 필드를 입력해주세요.");
            return;
        }

        const formData = new FormData();
        images.forEach((image) => {
            formData.append('imageFiles', image);
        });

        const requestDto = JSON.stringify(({
            title,
            content,
            place: locationObj.title.replace(/<\/?b>/g, ""),
            address: locationObj.roadAddress,
            date
        }));

        formData.append('requestDto', new Blob([requestDto], {
            type: "application/json",
        }));

        try {
            await axios.put(`http://localhost:8080/story?storyId=${story_id}`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${accessToken}`
                }
            });
            setTitle("");
            setDate(new Date());
            setLocation("");
            setLocationObj(null);
            setContent("");
            setImages([]);
            setPreviewURLs(['/images/anonymous.png']);

            window.alert("😎수정이 완료되었습니다😎");
            navigate("/main/storyList?page=1");
        } catch (error) {
            console.error("Error submitting form: ", error);
        }
    };

    const handlePrevImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex === 0 ? previewURLs.length - 1 : prevIndex - 1));
    };

    const handleNextImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex === previewURLs.length - 1 ? 0 : prevIndex + 1));
    };

    useEffect(() => {
        if (location && typeof location === 'string' && location.trim() === "") {
            setSearchResults([]);
        }
    }, [location]);

    return (
        <div className="diary-entry-page">
            <div className="top-section">
                <div className="form-left">
                    <TextField
                        label="제목"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        fullWidth
                    />
                    <Calendar
                        value={date}
                        onChange={setDate}
                    />
                </div>
                <div className="form-right">
                    <div className="image-upload">
                        <input
                            type="file"
                            accept="image/*"
                            multiple
                            onChange={handleImageChange}
                            style={{display: "none"}}
                            id="image-input"
                        />
                        <Button
                            variant="outlined"
                            color="primary"
                            onClick={() => document.getElementById("image-input").click()}
                        >
                            😎사진 고르기😎
                        </Button>
                        <div className="image-preview-container">
                            <img
                                src={previewURLs[currentImageIndex]}
                                alt="Preview"
                                className="preview-image"
                            />
                        </div>
                        <div className="image-nav-buttons">
                            <Button className="nav-button" onClick={handlePrevImage}
                                    disabled={images.length <= 1}>⬅️</Button>
                            <Button className="nav-button" onClick={handleNextImage}
                                    disabled={images.length <= 1}>➡️</Button>
                        </div>
                    </div>
                </div>
            </div>
            <div className="form-center">
                <div className="location-search">
                    <TextField
                        label="지역"
                        value={location}
                        onChange={(e) => {
                            setLocation(e.target.value);
                            handleSearch(e.target.value);
                        }}
                        fullWidth
                    />
                    {searchResults.length > 0 && (
                        <div id="resultContainer">
                            {searchResults.length > 0 ? (
                                <List>
                                    {searchResults.map((item, index) => (
                                        <ListItem button key={index} onClick={() => handleResultClick(item)}>
                                            <ListItemText
                                                primary={item.title.replace(/<\/?b>/g, "")}
                                                secondary={item.address}
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                            ) : (
                                <p>No results found</p>
                            )}
                        </div>
                    )}
                </div>
                <TextField
                    label="내용"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    multiline
                    rows={4}
                    fullWidth
                />
            </div>
            <div className="form-bottom">
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSubmit}
                >
                    수정하기
                </Button>
            </div>
        </div>
    );
};

export default EditStory;
