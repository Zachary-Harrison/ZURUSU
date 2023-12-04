const axios = require('axios');
const moment = require('moment');
const qs = require('qs');

// defining constant variables
const BASE_URL = '[BASE URL]'; // replace this with the URL for your project
const PRODUCTS = [
  '0PUK6V6EV0','1YMWWN1N4O','2ZYFJ3GM2N','66VCHSJNUP',
  '6E92ZMYYFZ','9SIQT8TOJO','L9ECAV7KIM','LS4PSXUNUM','OLJCESPC7Z'
];
const QUANTITIES = [1, 2, 3, 4, 5, 10];
const CURRENCIES = ['EUR', 'USD', 'JPY', 'GBP', 'TRY', 'CAD'];
const HOW_MANY_TIMES = 1;

// ==================================
// helper functions for HTTP Requests
// ==================================
function getRandomQuantity() {
  return QUANTITIES[Math.floor(Math.random() * QUANTITIES.length)];
}

function getRandomProduct() {
  return PRODUCTS[Math.floor(Math.random() * PRODUCTS.length)];
}

function getRandomCurrency() {
  return CURRENCIES[Math.floor(Math.random() * CURRENCIES.length)];
}

function timeIsBetween(start, end) {
  const now = moment.utc();
  return now.isBetween(moment(start), moment(end));
}


// ==================================
// functions to make HTTP requests
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

async function main() {
  while (true)
  {
    await index();
    await viewCart();
    await setCurrency();
    await browseProduct();
    await addToCart();
    await checkout();
  }
}

main();