import React, { createContext, useState } from 'react';

const DataContext = createContext();

function DataProvider({ children }) {
  const [data, setData] = useState([]);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [keywords, setKeywords] = useState([])
  const [results, setResults] = useState([])

  return (
    <DataContext.Provider value={{ data, setData, username, setUsername, password, setPassword, isLoggedIn, setIsLoggedIn }}>
      {children}
    </DataContext.Provider>
  );
}

export { DataContext, DataProvider };