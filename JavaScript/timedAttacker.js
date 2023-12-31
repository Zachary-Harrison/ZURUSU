const axios = require('axios');
const moment = require('moment');
const qs = require('qs');

const BASE_URL = "http://EXTERNAL_IP";
const ATTACKS = [
  { start: "2023-11-12T03:00:00Z", end: "2023-11-12T03:59:00Z", isActive: false, task: index },
  { start: "2023-11-12T06:00:00Z", end: "2023-11-12T06:59:00Z", isActive: false, task: viewCart },
  { start: "2023-11-12T09:00:00Z", end: "2023-11-12T09:59:00Z", isActive: false, task: setCurrency },
  { start: "2023-11-12T12:00:00Z", end: "2023-11-12T12:59:00Z", isActive: false, task: browseProduct },
  { start: "2023-11-12T15:00:00Z", end: "2023-11-12T15:59:00Z", isActive: false, task: addToCart },
  { start: "2023-11-12T18:00:00Z", end: "2023-11-12T18:59:00Z", isActive: false, task: checkout },
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

async function setCurrency() {
  await axios.post(`${BASE_URL}/setCurrency`, qs.stringify({
    'currency_code': CURRENCIES[Math.floor(Math.random() * CURRENCIES.length)],
  }));
}

async function browseProduct() {
  const product = PRODUCTS[Math.floor(Math.random() * PRODUCTS.length)];
  await axios.get(`${BASE_URL}/product/${product}`);
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
  while (true) {
    for (const atk of ATTACKS) {
      if (timeIsBetween(atk.start, atk.end)) {
        if (!atk.isActive) {
          atk.isActive = true;
          console.log(`Starting attack at ${moment().format('MM/DD/YYYY, h:mm A')}`);
        }
        try {
          await atk.task();
        } catch ({name, message}){
          console.log(`\tEncountered ${name}`);
        }
      } else if (atk.isActive) {
        atk.isActive = false;
        console.log(`Ending attack at ${moment().format('MM/DD/YYYY, h:mm A')}`);
      }
    }
  }
}

main();