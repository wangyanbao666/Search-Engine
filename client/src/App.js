import logo from './logo.svg';
import './App.css';
import SearchBar from './components/searchBar';

const user = {
  name: 'Hedy Lamarr',
  imageUrl: 'https://i.imgur.com/yXOvdOSs.jpg',
  imageSize: 90,
};

function App() {
  return (
    <div className="App">
      <header></header>
      <div className="contain">
        <div className="animate-charcter"> Search Engine </div>
        <SearchBar></SearchBar>
      </div>
    </div>
  );
}

export default App;
