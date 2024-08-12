import {Pagination} from "@mui/material";
import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {Card} from "./Card";
import "../../css/StoryList.scss"
import instance from "../main/axios/TokenInterceptor";

const StoryList = () => {

    const [pageCount, setPageCount] = useState(0);
    const [storyList, setStoryList] = useState([]);
    const [searchParams, setSearchParams] = useSearchParams();

    // 렌더링 되고 한번만 전체 게시물 갯수 가져와서 페이지 카운트 구하기
    // 렌더링 되고 한번만 페이지에 해당하는 게시물 가져오기
    useEffect(() => {
        const getStoryList = async () => {
            try {
                const page_number = searchParams.get("page");
                const response = await instance.get(`http://localhost:8080/story/paging?page_number=${page_number}&page_size=4`);
                if (response.data.isSuccess) {
                    const stories = response.data.result;
                    const storyIds = stories.map(story => story.id);

                    const response2 = await Promise.all(storyIds.map(async (id) =>
                        instance.get(`http://localhost:8080/storyImage?storyId=${id}`)
                    ));
                    const imageUrls = response2.map(response => response.data.result);


                    const response3 = await Promise.all(imageUrls.map(async (imageUrl) =>
                        instance.get(`http://localhost:8080${imageUrl}`, {
                            responseType: 'blob'
                        })));
                    const imageResponses = response3.map(response => response.data);

                    // 모든 이미지 요청이 완료될 때까지 대기
                    // const imageResponses = await Promise.all(imageRequests);
                    // 각 이미지 요청의 응답에서 URL을 추출
                    const imageMap = new Map(
                        imageResponses.map((response, index) => [storyIds[index], URL.createObjectURL(response)])
                    );
                    // 이야기와 이미지 URL을 결합
                    const combinedData = stories.map(story => ({
                        ...story,
                        img_url: imageMap.get(story.id) || ''
                    }));

                    setStoryList(combinedData);

                    const totalResponse = await instance.get("http://localhost:8080/story/count");
                    if (totalResponse.data.isSuccess)
                        setPageCount(Math.ceil(totalResponse.data / 4));
                    else {
                        console.error("일기 개수 불러오기 오류");
                        console.log(totalResponse.data.code);
                        console.log(totalResponse.data.message);
                    }

                } else {
                    console.error("일기 정보 불러오기 오류");
                    console.log(response.data.code);
                    console.log(response.data.message);
                }

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