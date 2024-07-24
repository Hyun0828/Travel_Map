import React, { useCallback, useState } from "react";
import Calendar from 'react-calendar';
import moment from 'moment';

const MapCalendar = () =>{
    const [useValue,setUseValue] = useState(new Date());

    return(
        <div>
            <Calendar onChange={setUseValue} value = {useValue}/>
            <div className="text-gray-500 mt-4">
                <p>현재 선택한 날짜:{moment(useValue).format("YYYY-MM-DD")}  </p>
            </div>
        </div>
    );
}

export default MapCalendar