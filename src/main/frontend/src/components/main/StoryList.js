import {Pagination} from "@mui/material";
import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import axios from "axios";
import {Card} from "./Card";
import "../../css/StoryList.scss"

const StoryList = () => {

    const [pageCount, setPageCount] = useState(0);
    const [storyList, setStoryList] = useState([]);
    const [searchParams, setSearchParams] = useSearchParams();
    const accessToken = localStorage.getItem('accessToken');

    // 렌더링 되고 한번만 전체 게시물 갯수 가져와서 페이지 카운트 구하기
    // 렌더링 되고 한번만 페이지에 해당하는 게시물 가져오기
    useEffect(() => {
        const getStoryList = async () => {
            try {
                const page_number = searchParams.get("page");
                const response = await axios.get(`http://localhost:8080/story/paging?page_number=${page_number}&page_size=4`, {
                    headers: {
                        'Authorization': `Bearer ${accessToken}`
                    },
                });
                const stories = response.data;
                const storyIds = stories.map(story => story.id);

                const imageRequests = storyIds.map(id =>
                    axios.get(`http://localhost:8080/storyImage?storyId=${id}`, {
                        headers: {
                            Authorization: `Bearer ${accessToken}`
                        },
                        responseType: 'blob', // 이미지를 바이너리 형식으로 받아옴
                    })
                );
                // 모든 이미지 요청이 완료될 때까지 대기
                const imageResponses = await Promise.all(imageRequests);
                // 각 이미지 요청의 응답에서 URL을 추출
                const imageMap = new Map(
                    imageResponses.map((response, index) => [storyIds[index], URL.createObjectURL(response.data)])
                );
                // 이야기와 이미지 URL을 결합
                const combinedData = stories.map(story => ({
                    ...story,
                    img_url: imageMap.get(story.id) || ''
                }));

                setStoryList(combinedData);

                const totalResponse = await axios.get("http://localhost:8080/story/count", {
                    headers: {
                        'Authorization': `Bearer ${accessToken}`
                    }
                });
                setPageCount(Math.ceil(totalResponse.data / 4));
            } catch (error) {
                console.error('데이터 로드 오류:', error);
            }
        };

        getStoryList();
    }, [])

    return (
        <div className="boardList-wrapper">
            <div className="boardList-header">
                일기 📝
            </div>
            <div className="boardList-body">
                {storyList.map((item, index) => (
                    <Card key={item.id} place={item.place} date={item.date}
                          title={item.title} content={item.content}
                          story_id={item.id} img_url={item.img_url}
                    />
                ))}
            </div>
            <div className="boardList-footer">
                {/*페이지네이션: count에 페이지 카운트, page에 페이지 번호 넣기*/}
                <Pagination
                    variant="outlined" color="primary" page={Number(searchParams.get("page"))}
                    count={pageCount} size="large"
                    onChange={(e, value) => {
                        window.location.href = `/main/storyList?page=${value}`;
                    }}
                    showFirstButton showLastButton
                />
            </div>
        </div>

    )
}

export default StoryList;