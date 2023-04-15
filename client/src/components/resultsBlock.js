export default function ResultBlock({info}){
    return (
        <div>
            <p>Title: {info.title}</p>
            <p>url: {info.url}</p>
        </div>
    )
}