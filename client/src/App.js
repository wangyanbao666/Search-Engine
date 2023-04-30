import logo from './logo.svg';
import './App.css';
import SearchPage from './components/searchPage';
import { DataProvider } from './components/dataContext';
import {
  BrowserRouter as Router,
  Routes,
  Route
 } from "react-router-dom";
import Results from './components/results';
import Login from './components/login';


function App() {
  return (
    <DataProvider>
      <Router>
        <Routes>
          <Route path="/" element={<SearchPage className={'animate-charcter'}/>} />
          <Route path="/result" element={<Results/>} />
          <Route path="/login" element={<Login/>}></Route>
        </Routes>
      </Router>
    </DataProvider>
  );
}

export default App;
