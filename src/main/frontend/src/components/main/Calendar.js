import React, {useEffect, useState} from 'react';
import {Icon} from '@iconify/react';
import {
    addDays,
    addMonths,
    endOfMonth,
    endOfWeek,
    format,
    isSameDay,
    isSameMonth,
    startOfMonth,
    startOfWeek,
    subMonths
} from 'date-fns';
import '../../css/Calendar.scss';
import {useNavigate} from "react-router-dom";
import instance from "../main/axios/TokenInterceptor";


const RenderHeader = ({currentMonth, prevMonth, nextMonth}) => {
    return (
        <div className="header row">
            <div className="col col-start">
              <span className="text">
                  <span className="text month">
                      {format(currentMonth, 'M')}월
                  </span>
                  {format(currentMonth, 'yyyy')}
              </span>
            </div>
            <div className="col col-end">
                <Icon icon="bi:arrow-left-circle-fill" onClick={prevMonth}/>
                <Icon icon="bi:arrow-right-circle-fill" onClick={nextMonth}/>
            </div>
        </div>
    );
};

const RenderDays = () => {
    const days = [];
    const date = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

    for (let i = 0; i < 7; i++) {
        const dayClass = i === 0 ? 'sunday' : i === 6 ? 'saturday' : '';
        days.push(
            <div className={`col ${dayClass}`} key={i}>
                {date[i]}
            </div>,
        );
    }

    return <div className="days row">{days}</div>
}


const RenderCells = ({currentMonth, selectedDate, storyList, storyClickHandler}) => {
    const monthStart = startOfMonth(currentMonth);
    const monthEnd = endOfMonth(monthStart);
    const startDate = startOfWeek(monthStart);
    const endDate = endOfWeek(monthEnd);
    const today = new Date();

    const rows = [];
    let days = [];
    let day = startDate;
    let formattedDate = '';

    while (day <= endDate) {
        for (let i = 0; i < 7; i++) {
            formattedDate = format(day, 'd');
            const cloneDay = day;
            const storiesForDay = storyList.filter(story =>
                isSameDay(new Date(story.date), cloneDay)
            );
            const isToday = isSameDay(day, today);
            const isSunday = format(day, 'E') === 'Sun';
            const isSaturday = format(day, 'E') === 'Sat';
            const dayClass = isSunday ? 'sunday' : isSaturday ? 'saturday' : '';

            days.push(
                <div
                    className={`col cell ${
                        !isSameMonth(day, monthStart)
                            ? 'disabled'
                            : isSameDay(day, selectedDate)
                                ? 'selected'
                                : format(currentMonth, 'M') !== format(day, 'M')
                                    ? 'not-valid'
                                    : 'valid'
                    }`}
                    key={day}
                >
                    <span
                        className={`day-number ${dayClass}`}
                    >
                        {formattedDate}
                    </span>
                    {isToday && <div className="today-label">Today</div>}
                    {storiesForDay.length > 0 && (
                        <div className="story-details">
                            {storiesForDay.map((story, index) => (
                                <div key={index} className="story-item" onClick={storyClickHandler(story.id)}>
                                    <strong>{story.title}</strong>
                                    <p>{story.place}</p>
                                </div>
                            ))}
                        </div>
                    )}
                </div>,
            );
            day = addDays(day, 1);
        }
        rows.push(
            <div className="row" key={day}>
                {days}
            </div>,
        );
        days = [];
    }
    return <div className="body">{rows}</div>;
};


export const Calendar = () => {
    const [currentMonth, setCurrentMonth] = useState(new Date());
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [storyList, setStoryList] = useState([]);
    const navigate = useNavigate();

    /**
     * DB에서 전체 일기를 가져오기
     */
    useEffect(() => {
        const fetchData = async () => {
            setStoryList([]);
            try {
                // const response = await axios.get("http://localhost:8080/story/all", {
                //     headers: {
                //         'Authorization': `Bearer ${accessToken}`
                //     }
                // });
                const response = await instance.get('http://localhost:8080/story/all');
                const storyInfoResponseDtos = response.data;

                for (const dto of storyInfoResponseDtos) {
                    const {id, title, place, date} = dto;
                    setStoryList(prevData => [...prevData, dto]);
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData();
    }, []);

    const prevMonth = () => {
        setCurrentMonth(subMonths(currentMonth, 1));
    };

    const nextMonth = () => {
        setCurrentMonth(addMonths(currentMonth, 1));
    }

    const storyClickHandler = (storyId) => () => {
        navigate(`/main/story/${storyId}`);
    }

    return (
        <div className="calendar">
            <RenderHeader
                currentMonth={currentMonth}
                prevMonth={prevMonth}
                nextMonth={nextMonth}
            />
            <RenderDays/>
            <RenderCells
                currentMonth={currentMonth}
                selectedDate={selectedDate}
                storyList={storyList}
                storyClickHandler={storyClickHandler}
            />
        </div>
    );
};

export default Calendar;