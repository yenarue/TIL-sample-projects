const express = require('express');
const router = express.Router();
const Products = require('../models/products');

router.get('/', (req, res) => {
    Products.find({}, (err, products) => {
        res.render('home', { products : products });
    })
});

module.exports = router;