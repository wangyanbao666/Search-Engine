import ResultBlock from "./resultsBlock"

export default function Results({results}){
    let key = 1;
    return (
        <div>
            {[...results].map(result => <ResultBlock info={result} key={key++}></ResultBlock>)}
        </div>
    )
}