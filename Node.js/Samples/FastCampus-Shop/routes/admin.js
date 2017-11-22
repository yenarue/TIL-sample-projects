const express = require('express');
const router = express.Router();
const Products = require('../models/products');
const Comments = require('../models/comments');

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
    res.render('admin/form', { product : "" });
});

router.post('/products/write', (req, res) => {
    const product = new Products({
        name : req.body.name,
        price : req.body.price,
        description : req.body.description
    });

    product.save((err) => {
        res.redirect('/admin/products');
    });
});

router.get('/products/detail/:id', (req, res) => {
    Products.findOne({ 'id': req.params.id }, (err, product) => {
        Comments.find({ product_id: req.params.id }, (err, comments) => {
            res.render('admin/productsDetail', { product: product, comments: comments });
        })
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
    });
});

router.get('/products/delete/:id', (req, res) => {
    Products.remove({ id : req.params.id }, err => {
        res.redirect('/admin/products');
    });
});

router.post('/products/comment/insert', (req, res) => {
    const comment = new Comments({
        content : req.body.content,
        product_id : parseInt(req.body.product_id),
    })

    comment.save((err, comment) => res.json({
        id : comment.id,
        content : comment.content,
        message : "success",   
    }));
});

module.exports = router;