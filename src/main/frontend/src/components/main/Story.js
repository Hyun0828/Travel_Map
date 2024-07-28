import React, {useContext, useEffect, useState} from "react";
import axios from "axios";
import {Button, List, ListItem, ListItemText, TextField} from "@mui/material";
import Calendar from 'react-calendar';
import {DataContext} from '../../contexts/DataContext';
import 'react-calendar/dist/Calendar.css';
import '../../css/Story.css';
import {useNavigate} from "react-router-dom";
import {useNavermaps} from 'react-naver-maps';

axios.defaults.withCredentials = true;

const Story = () => {
    const {totalDataArray, setTotalDataArray} = useContext(DataContext);
    const [title, setTitle] = useState("");
    const [date, setDate] = useState(new Date());
    const [location, setLocation] = useState("");
    const [locationObj, setLocationObj] = useState(null);
    const [content, setContent] = useState("");
    const [images, setImages] = useState([]);
    const [previewURLs, setPreviewURLs] = useState(['/images/anonymous.png']);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [searchResults, setSearchResults] = useState([]);
    const navermaps = useNavermaps();
    const accessToken = localStorage.getItem('accessToken');
    const navigate = useNavigate();

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
        if (locationObj) {
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
            }))

            formData.append('requestDto', new Blob([requestDto],
                {
                    type: "application/json",
                }),
            );

            const roadAddress = locationObj.roadAddress;
            const temp_title = locationObj.title.replace(/<\/?b>/g, "");

            try {
                await axios.post("http://localhost:8080/story", formData, {
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

                handleGeocode(roadAddress, temp_title);

                navigate("/main/map");
            } catch (error) {
                console.error("Error submitting form: ", error);
            }
        } else {
            console.error("No location selected");
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

    /**
     * 위도, 경도 추출하고 데이터 저장
     */
    const handleGeocode = (roadAddress, title) => {
        if (!navermaps || !navermaps.Service) {
            console.error('Naver Maps service is not available');
            return;
        }

        navermaps.Service.geocode(
            { address: roadAddress },
            (status, response) => {
                if (status !== navermaps.Service.Status.OK) {
                    console.error('Geocoding error:', status);
                    return alert('Something went wrong during geocoding.');
                }

                const result = response.result;
                const items = result.items;
                if (items.length > 0) {
                    const { x: lng, y: lat } = items[0].point;

                    const newData = {
                        dom_id: totalDataArray.length + 1,
                        title: title,
                        lat: lat,
                        lng: lng
                    };

                    console.log(lat);
                    console.log(lng);
                    setTotalDataArray(prevArray => [...prevArray, newData]);
                } else {
                    console.error('No geocoding results found.');
                    alert('No geocoding results found.');
                }
            }
        );
    };

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

export default Story;
