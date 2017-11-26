const express = require('express');
const router = express.Router();
const Contacts = require('../models/contacts');

// /contacts/detail/:id  상세글보기
// /contacts/edit/:id 글수정하기
// /contacts/delete/:id 글삭제하기
router.get('/', (req, res) => {
    Contacts.find({}, (err, contacts) => {
        res.render('contacts/list', {
            contacts: contacts
        });
    });
});

router.get('/write', (req, res) => {
    res.render('contacts/form');
})

router.post('/write', (req, res) => {
    const contacts = new Contacts({
        title : req.body.title,
        author : req.body.author,
        contents : req.body.contents
    });

    contacts.save((err) => {
        res.redirect('/contacts/');
    });
})

router.get('/detail/:seq', (req, res) => {
    Contacts.findOne({ seq : req.params.seq }, (err, contact) => {
        res.render('contacts/detail', {
            contact : contact
        })
    })
});

module.exports = router;