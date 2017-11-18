var express = require('express');
var router = express.Router();

router.get('/', (req, res) => {
    res.send('It is a admin app!');
});

router.get('/products', (req, res) => {
    res.send('admin product page');
});

module.exports = router;