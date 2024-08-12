import React, {createContext, useState} from 'react';

export const DataContext = createContext();

export const DataProvider = ({children}) => {
    const [totalDataArray, setTotalDataArray] = useState([]);

    return (
        <DataContext.Provider value={{totalDataArray, setTotalDataArray}}>
            {children}
        </DataContext.Provider>
    );
};