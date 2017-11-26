const express = require('express');
const router = express.Router();
const Contacts = require('../models/contacts');

// /contacts  글리스트
// /contacts/write 글작성
// /contacts/detail/:id  상세글보기
// /contacts/edit/:id 글수정하기
// /contacts/delete/:id 글삭제하기
router.get('/', (req, res) => {
    Contacts.find({}, (err, contacts) => {
        res.render('contacts/list', {
            contacts: contacts
        });
    });
})

module.exports = router;