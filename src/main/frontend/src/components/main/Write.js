import React, {useEffect, useState} from "react";
import {Button, List, ListItem, ListItemText, TextField} from "@mui/material";
import Calendar from 'react-calendar';
import {format} from 'date-fns';
import 'react-calendar/dist/Calendar.css';
import '../../css/Write.css';
import {useNavigate} from "react-router-dom";
import instance from "../main/axios/TokenInterceptor";


const Write = () => {
    const [title, setTitle] = useState("");
    const [date, setDate] = useState(new Date());
    const [location, setLocation] = useState("");
    const [locationObj, setLocationObj] = useState(null);
    const [content, setContent] = useState("");
    const [images, setImages] = useState([]);
    const [previewURLs, setPreviewURLs] = useState(['/images/anonymous.png']);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [searchResults, setSearchResults] = useState([]);
    const navigate = useNavigate();

    const handleSearch = async (text) => {
        if (text && typeof text === 'string' && text.trim() !== "") {
            try {
                const response = await instance.get('http://localhost:8080/naver/search', {
                    params: {text},
                    // headers: {
                    //     "X-Naver-Client-Id": process.env.REACT_APP_NAVER_CLIENT_ID,
                    //     "X-Naver-Client-Secret": process.env.REACT_APP_NAVER_CLIENT_SECRET
                    // }
                });
                if (response.data.isSuccess) {
                    const result = JSON.parse(response.data.result); // JSON 파싱
                    setSearchResults(result.items);
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

    const handleImageChange = (e) => {
        e.preventDefault();
        const files = Array.from(e.target.files);
        const newPreviewURLs = files.map(file => URL.createObjectURL(file));
        setImages(files);
        setPreviewURLs(newPreviewURLs);
        setCurrentImageIndex(0);
    };

    const handleResultClick = (item) => {
        if (item) {
            setLocation(item.title.replace(/<\/?b>/g, ""));
            setLocationObj(item);
        }
        setSearchResults([]);
    };

    const handleSubmit = async () => {
        if (!title || !location || !content) {
            alert("모든 필드를 입력해주세요.");
            return;
        }

        if (locationObj) {
            const formData = new FormData();

            images.forEach((image) => {
                formData.append('imageFiles', image);
            });
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
                const response = await instance.post('http://localhost:8080/story', formData, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    }
                })

                if (response.data.isSuccess) {
                    setTitle("");
                    setDate(new Date());
                    setLocation("");
                    setLocationObj(null);
                    setContent("");
                    setImages([]);
                    setPreviewURLs(['/images/anonymous.png']);

                    navigate("/main/map");
                } else {
                    console.error("일기 작성 실패");
                    console.log(response.data.code);
                    console.log(response.data.message);
                }


            } catch (error) {
                console.error("Error submitting form: ", error);
            }
        } else {
            console.error("No location selected");
            alert("지역을 선택해주세요.");
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
                    저장하기
                </Button>
            </div>
        </div>
    );
};

export default Write;
