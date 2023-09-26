const buffer = [];
let isStream = true;

async function readData(url, qu) {
  const response = await fetch(url)
  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  let result = await reader.read()
  while (!result.done) {
    const text = decoder.decode(result.value)
    qu.push(text)
    console.log(text)
    result = await reader.read()
  }
  isStream= false;
}

async function displayData(qu) {
    while (isStream) {
        const sleep = ms => new Promise(r => setTimeout(r, ms));
        if (qu.length > 0) {
            document.getElementById("random").innerHTML=qu.shift();
        }
        await sleep(1000/40)
    }
}

readData("/api/stream-random", buffer);
displayData(buffer)


