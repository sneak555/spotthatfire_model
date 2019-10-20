const express = require('express')
const app = express()
const PORT = 4002

app.use(express.static(__dirname + '/./client/dist'))

app.use((req, res, next)=>{
  res.sendStatus(400)
})

app.use((err, req, res, next)=>{
  console.log(err)
  res.sendStatus(500).send(err.stack)
})

app.listen(PORT, () => {
  console.log(`Server is now listening on port ${PORT}!`)
})