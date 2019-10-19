// use as template/guide to seed and generate json/csv file

const fs = require('fs')
const { Generator } = require('./dataGenerator')
const home = require('os').homedir()
// change to your local directory 
var path = home + '/Coding/db/data/' 

let start = 1
let max = 4
let current = 1
let count = 0
console.time('Data for PostgreSQL completed in');

let seedEntry = (start, max, current) => {
  if (start > max) {
    console.timeEnd('Data for PostgreSQL completed in')
    current = 1
    return
  } else {

    let string = ""
    // determine number of data records generated 
    // seed in chuncks of a maximum 500000
    for(let i = 0; i <= 500000; i++) {
      let generatedData = Generator()
      let entry = `${count},${generatedData.name},${generatedData.price},${generatedData.store},${generatedData.returnDays},${generatedData.itemID}`

      string += entry + '\n'
      current++
      count++
    }

    fs.appendFile(path + 'mockData.json', string, (err) => {
      if (err) throw err
      console.log('Finished loop ' + start)
      start++;
      seedEntry(start, max, current)
    })
  }
}

seedEntry(start, max, current)