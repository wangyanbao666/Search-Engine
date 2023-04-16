import { useContext } from "react";
import ResultBlock from "./resultsBlock"
import { DataContext } from "./dataContext";
import SearchPage from "./searchPage";

export default function Results(){
    const { data } = useContext(DataContext)
    console.log(data)
    let key = 1;
    return (
        <div className="resultPage">
            <SearchPage className={'animate-charcter-in-result'}></SearchPage>
            <div className="container">
                {[...data].map(result => <ResultBlock info={result} key={key++}></ResultBlock>)}
            </div>
        </div>
    )
}