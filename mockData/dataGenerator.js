// used faker before, just change to RESTful API made for data 
// in this example was using faker.js api - use the RESTful api we make locally 
// Use as template/guide

const faker = require('faker');

// change ending values after entry... such as itemID etc
exports.Generator = () => {

  const entry = {}

  entry.itemID = faker.finance.account()

  entry.name = faker.commerce.productName()

  entry.price = faker.commerce.price()

  entry.store = faker.company.companyName()

  entry.returnDays = faker.random.number()

  return entry
}