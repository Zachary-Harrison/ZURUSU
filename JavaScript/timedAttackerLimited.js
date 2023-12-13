const axios = require('axios');
const moment = require('moment');
const qs = require('qs');

// retrieving BASE_URL from provided arguments
const BASE_URL = "http://EXTERNAL_IP";
const ATTACKS = [  // cost = # of services affected; freq = distribution of pages visited in normal behavior
  { start: "2023-12-13T03:00:00Z", end: "2023-12-13T03:59:00Z", task: index,          cost: 5,  freq: 1 },
  { start: "2023-12-13T06:00:00Z", end: "2023-12-13T06:59:00Z", task: viewCart,       cost: 6,  freq: 3 },
  { start: "2023-12-13T09:00:00Z", end: "2023-12-13T09:59:00Z", task: setCurrency,    cost: 5,  freq: 2 },
  { start: "2023-12-13T12:00:00Z", end: "2023-12-13T12:59:00Z", task: browseProduct,  cost: 6,  freq: 10 },
  { start: "2023-12-13T15:00:00Z", end: "2023-12-13T15:59:00Z", task: addToCart,      cost: 6,  freq: 2 },
  { start: "2023-12-13T18:00:00Z", end: "2023-12-13T18:59:00Z", task: checkout,       cost: 10, freq: 1 },
];

const PRODUCTS = [
  '0PUK6V6EV0','1YMWWN1N4O','2ZYFJ3GM2N','66VCHSJNUP',
  '6E92ZMYYFZ','9SIQT8TOJO','L9ECAV7KIM','LS4PSXUNUM','OLJCESPC7Z'
];
const QUANTITIES = [1, 2, 3, 4, 5, 10];
const CURRENCIES = ['EUR', 'USD', 'JPY', 'GBP', 'TRY', 'CAD'];

// ==================================
// these functions make HTTP requests
// ==================================
async function index() {
  await axios.get(`${BASE_URL}/`);
}

async function viewCart() {
  await axios.get(`${BASE_URL}/cart`);
}

async function browseProduct() {
  const product = PRODUCTS[Math.floor(Math.random() * PRODUCTS.length)];
  await axios.get(`${BASE_URL}/product/${product}`);
}

async function setCurrency() {
  await axios.post(`${BASE_URL}/setCurrency`, qs.stringify({
    'currency_code': CURRENCIES[Math.floor(Math.random() * CURRENCIES.length)],
  }));
}

async function addToCart() {
  const product = PRODUCTS[Math.floor(Math.random() * PRODUCTS.length)];
  await axios.get(`${BASE_URL}/product/${product}`);
  await axios.post(`${BASE_URL}/cart`, qs.stringify({
    'product_id': product,
    'quantity': QUANTITIES[Math.floor(Math.random() * QUANTITIES.length)],
  }));
}

async function checkout() {
  await addToCart();
  await axios.post(`${BASE_URL}/cart/checkout`, qs.stringify({
    'email': 'scary@attacker.com',
    'street_address': '666 Amphitheatre Parkway',
    'zip_code': '66666',
    'city': 'Attack City',
    'state': 'Attack State',
    'country': 'Attack Country',
    'credit_card_number': '4111-1111-1111-1111', // Visa test card number
    'credit_card_expiration_month': '12',
    'credit_card_expiration_year': '2025',
    'credit_card_cvv': '123',
  }));
}

function timeIsBetween(start, end) {
  const now = moment.utc();
  return now.isBetween(moment(start), moment(end));
}

async function main() {
  for (const atk of ATTACKS) {
    atk.isActive = false;
  }

  // normalizing freq (total = 1)
  freqTotal = 0;
  for (let i = 0; i < ATTACKS.length; i++) {
    freqTotal += ATTACKS[i].freq;
  }
  for (let i = 0; i < ATTACKS.length; i++) {
    ATTACKS[i].freq /= freqTotal;
  }
  
  // figuring out how much normal behavior "costs" per request, on average
  normalExpectedCost = 0
  for (let i = 0; i < ATTACKS.length; i++) {
    normalExpectedCost += ATTACKS[i].cost * ATTACKS[i].freq;
  }
  
  minWait = 1_000;
  maxWait = 10_000;
  while (true) {
    for (const atk of ATTACKS) {
      if (timeIsBetween(atk.start, atk.end)) {
        if (!atk.isActive) {
          atk.isActive = true;
          console.log(`Starting attack at ${moment().format('MM/DD/YYYY, h:mm A')}`);
        }
        const randNumGen = Math.floor(Math.random() * (maxWait - minWait)) + minWait
        const waitTime = (2 / 10) * (atk.cost / normalExpectedCost) * randNumGen;  // 1.5 output of 10 users
        const startTime = Date.now();
        await atk.task();
        const elapsedTime = Date.now() - startTime;
        if (elapsedTime < waitTime) {
          await new Promise(resolve => setTimeout(resolve, waitTime - elapsedTime));
        }
      } else if (atk.isActive) {
        atk.isActive = false;
        console.log(`Ending attack at ${moment().format('MM/DD/YYYY, h:mm A')}`);
      }
    }
  }
}
  
  main();