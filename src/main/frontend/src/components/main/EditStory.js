import React, {useEffect, useState} from "react";
import {Button, List, ListItem, ListItemText, TextField} from "@mui/material";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../../css/EditStory.css';
import {useNavigate, useParams} from "react-router-dom";
import instance from "../main/axios/TokenInterceptor";
import {format} from "date-fns";

const EditStory = () => {
    const {story_id} = useParams();
    const [title, setTitle] = useState("");
    const [date, setDate] = useState(new Date());
    const [location, setLocation] = useState("");
    const [locationObj, setLocationObj] = useState(null);
    const [content, setContent] = useState("");
    const [images, setImages] = useState([]);
    const [previewImages, setPreviewImages] = useState([]);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [searchResults, setSearchResults] = useState([]);
    const navigate = useNavigate();

    /**
     * 네이버 검색 api
     */
    const handleSearch = async (text) => {
        if (text && typeof text === 'string' && text.trim() !== "") {
            try {
                const response = await instance.get('http://localhost:8080/naver/search', {
                    params: {text},
                    headers: {
                        "X-Naver-Client-Id": process.env.REACT_APP_NAVER_CLIENT_ID,
                        "X-Naver-Client-Secret": process.env.REACT_APP_NAVER_CLIENT_SECRET
                    }
                });

                if (response.data.isSuccess) {
                    setSearchResults(response.data.items);
                } else {
                    console.error("네이버 검색 api 에러")
                    console.log(response.data.code);
                    console.log(response.data.message);
                }
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
        setImages(files);

        // 이미지 미리보기를 위한 URL 생성
        const previewUrls = files.map((file) => URL.createObjectURL(file));
        setCurrentImageIndex(0);
        setPreviewImages(previewUrls); // 미리보기용 URL 설정
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
            const response = await instance.get(`http://localhost:8080/story?storyId=${story_id}`);
            if (response.data.isSuccess)
                return response.data.result;
            else {
                console.error("일기 정보 가져오기 오류");
                console.log(response.data.code);
                console.log(response.data.message);
                return null;
            }
        }

        const getImages = async () => {
            const response = await instance.get(`http://localhost:8080/storyImages?storyId=${story_id}`);
            if (response.data.isSuccess) {
                const imageUrls = await Promise.all(
                    response.data.result.map(async (image) => {
                        const imgResponse = await instance.get(`http://localhost:8080${image}`, {
                            responseType: 'blob'
                        });
                        return URL.createObjectURL(imgResponse.data);
                    })
                );
                setPreviewImages(imageUrls);
                setImages(imageUrls);
            } else {
                console.error("일기 이미지 불러오기 오류");
                console.log(response.data.code);
                console.log(response.data.message);
            }
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
        getImages();
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

        for (let image of images) {
            let blob;
            if (typeof image === 'string')
                blob = await convertUrlToBlob(image);
            else
                blob = image;
            formData.append('imageFiles', blob);
        }

        const formattedDate = format(date, "yyyy-MM-dd'T'HH:mm:ss");
        const requestDto = JSON.stringify(({
            title,
            content,
            place: locationObj.title.replace(/<\/?b>/g, ""),
            address: locationObj.roadAddress,
            date: formattedDate
        }));

        formData.append('requestDto', new Blob([requestDto], {
            type: "application/json",
        }));

        try {
            const response = await instance.put(`http://localhost:8080/story?storyId=${story_id}`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            })
            if (response.data.isSuccess) {
                setTitle("");
                setDate(new Date());
                setLocation("");
                setLocationObj(null);
                setContent("");
                setImages([]);
                setPreviewImages([]);

                window.alert("😎수정이 완료되었습니다😎");
                navigate("/main/storyList?page=1");
            } else {
                console.error("일기 수정 실패");
                console.log(response.data.code);
                console.log(response.data.message);
            }

        } catch (error) {
            console.error("Error submitting form: ", error);
        }
    };

    const handleNextImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex + 1) % images.length);
    };

    const handlePrevImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex - 1 + images.length) % images.length);
    };

    const convertUrlToBlob = async (url) => {
        try {
            const response = await fetch(url);
            const blob = await response.blob();
            return blob;
        } catch (error) {
            console.error('Error converting URL to Blob:', error);
            return null;
        }
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
                            {previewImages.length > 0 && (
                                <img
                                    src={previewImages[currentImageIndex]}
                                    alt={`Story image ${currentImageIndex + 1}`}
                                />
                            )}
                        </div>
                        <div className="image-nav-buttons">
                            <Button className="nav-button" onClick={handlePrevImage}>⬅️</Button>
                            <Button className="nav-button" onClick={handleNextImage}>➡️</Button>
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
