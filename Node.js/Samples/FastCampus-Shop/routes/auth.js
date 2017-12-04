const express = require('express');
const router = express.Router();
const passport = require('passport');
const FacebookStrategy = require('passport-facebook').Strategy;
const Users = require('../models/users');
const config = require('../config');

passport.serializeUser((user, done) => {
    done(null, user);
});

passport.deserializeUser((user, done) => {
    done(null, user);
});

passport.use(new FacebookStrategy({
        clientID: config.facebook.client_id,
        clientSecret: config.facebook.client_secret,
        callbackURL: "http://localhost:3000/auth/facebook/callback",
        profileFields: ['id', 'displayName', 'photos', 'email']
    }, (accessToken, refreshToken, profile, done) => {
        console.log(profile);
        //console.log(profile.displayName);
        //console.log(profile.emails[0].value);
        //console.log(profile._raw);
        //console.log(profile._json);
        Users.findOne({ username : "fb_" + profile.id }, (err, user) => {
            if (!user) {
                var regData = {
                    username :  "fb_" + profile.id,
                    type : "facebook_login",
                    displayname : profile.displayName
                };
                var user = new Users(regData);
                user.save(err => {
                    done(null, regData); //세션 등록
                });
            } else { //있으면 DB에서 가져와서 세션등록
                done(null, user);
            }
        });
    }
));

// http://localhost:3000/auth/facebook 접근시 facebook으로 넘길 url 작성해줌
router.get('/facebook', passport.authenticate('facebook', { scope: 'email'}) );

//인증후 페이스북에서 이 주소로 리턴해줌. 상단에 적은 callbackURL과 일치
router.get('/facebook/callback',
    passport.authenticate('facebook', { 
            successRedirect: '/auth/facebook/success',
            failureRedirect: '/auth/facebook/fail' 
        }));

router.get('/facebook/success', function(req,res){
    res.send(req.user);
});

router.get('/facebook/fail', function(req,res){
    res.send('facebook login fail');
});

module.exports = router;