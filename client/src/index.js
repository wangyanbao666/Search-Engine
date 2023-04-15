import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import './searchBar.css'
import App from './App';
import {
  BrowserRouter as Router,
  Routes,
  Route
 } from "react-router-dom";
import reportWebVitals from './reportWebVitals';
import Results from './components/results';

const root = ReactDOM.createRoot(document.getElementById('root'));
const sample = [
  {
    title: "ttt",
    url: "uuu",
  },
  {
    title: "eee",
    url: "lll",
  },

]
root.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/result" element={<Results results={sample}/>} />
      </Routes>
    </Router>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
