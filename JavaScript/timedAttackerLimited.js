const axios = require('axios');
const moment = require('moment');
const qs = require('qs');

// retrieving BASE_URL from provided arguments
const BASE_URL = process.argv[2];
if (!BASE_URL) {
  console.error('Please provide a BASE_URL as an argument');
  process.exit(1);
}

const PRODUCTS = [
  '0PUK6V6EV0','1YMWWN1N4O','2ZYFJ3GM2N','66VCHSJNUP',
  '6E92ZMYYFZ','9SIQT8TOJO','L9ECAV7KIM','LS4PSXUNUM','OLJCESPC7Z'
];
const QUANTITIES = [1, 2, 3, 4, 5, 10];
const CURRENCIES = ['EUR', 'USD', 'JPY', 'GBP', 'TRY', 'CAD'];
// Example:
// const ATTACKS = [ 
//   { start: "2023-11-12T03:00:00Z", end: "2023-11-12T03:59:00Z", task: index },
//   { start: "2023-11-12T06:00:00Z", end: "2023-11-12T06:59:00Z", task: viewCart },
//   { start: "2023-11-12T09:00:00Z", end: "2023-11-12T09:59:00Z", task: setCurrency },
//   { start: "2023-11-12T12:00:00Z", end: "2023-11-12T12:59:00Z", task: browseProduct },
//   { start: "2023-11-12T15:00:00Z", end: "2023-11-12T15:59:00Z", task: addToCart },
//   { start: "2023-11-12T18:00:00Z", end: "2023-11-12T18:59:00Z", task: checkout },
// ];
const ATTACKS = [ 
    { start: "2023-11-13T23:00:00Z", end: "2023-11-14T00:59:00Z", task: index },
    { start: "2023-11-14T02:00:00Z", end: "2023-11-14T02:59:00Z", task: viewCart },
    { start: "2023-11-14T05:00:00Z", end: "2023-11-14T05:59:00Z", task: setCurrency },
    { start: "2023-11-14T08:00:00Z", end: "2023-11-14T08:59:00Z", task: browseProduct },
    { start: "2023-11-14T11:00:00Z", end: "2023-11-14T11:59:00Z", task: addToCart },
    { start: "2023-11-14T14:00:00Z", end: "2023-11-14T14:59:00Z", task: checkout },
  ];

// ==================================
// these functions make HTTP requests
// ==================================
async function index() {
  await axios.get(BASE_URL + '/');
}

async function viewCart() {
  await axios.get(BASE_URL + '/cart');
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
  
    while (true) {
      for (const atk of ATTACKS) {
        if (timeIsBetween(atk.start, atk.end)) {
          if (!atk.isActive) {
            atk.isActive = true;
            console.log(`Starting attack at ${moment().format('MM/DD/YYYY, h:mm A')}`);
          }
          // normal behavior is ~0.55 requests/sec, so make half as many requests by doubling wait time
          const startTime = Date.now();
          await atk.task();
          const elapsedTime = Date.now() - startTime;
          if (elapsedTime < 1000) {
            await new Promise(resolve => setTimeout(resolve, 1100 - elapsedTime));
          }
        } else if (atk.isActive) {
          atk.isActive = false;
          console.log(`Ending attack at ${moment().format('MM/DD/YYYY, h:mm A')}`);
        }
      }
    }
  }
  
  main();