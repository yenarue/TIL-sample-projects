const express = require('express');
const router = express.Router();
const Products = require('../models/products');

router.get('/', (req, res) => {
    res.send('It is a admin app!');
});

router.get('/products', (req, res) => {
    Products.find({}, (err, products) => {
        res.render('admin/products', {
            products : products
        });
    });
});

router.get('/products/write', (req, res) => {
    res.render('admin/form');
});

router.post('/products/write', (req, res) => {
    const product = new Products({
        name : req.body.name,
        price : req.body.price,
        description : req.body.description
    });

    product.save((err) => {
        res.redirect('/admin/products', { product : "" });
    });
});

router.get('/products/detail/:id', (req, res) => {
    Products.findOne(
        { 'id': req.params.id },
        (err, product) => {
            res.render('admin/productsDetail', { product: product });
        });
});

router.get('/products/edit/:id', (req, res) => {
    Products.findOne(
        { 'id': req.params.id },
        (err, product) => {
            res.render('admin/form', { product: product });
        });
});

router.post('/products/edit/:id', (req, res) => {
    const product = {
        name : req.body.name,
        price : req.body.price,
        description : req.body.description
    };

    Products.update({ 'id' : req.params.id }, { $set : product }, err => {
        res.redirect('/admin/products/detail/' + req.params.id);
    })
});

module.exports = router;