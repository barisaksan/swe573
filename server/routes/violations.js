var express = require('express');
var router = express.Router();

/*
 * GET userlist.
 */
router.get('/', function(req, res) {
    var db = req.db;
    var collection = db.get('violations');
    collection.find({},{},function(e,docs){
        res.json(docs);
    });
});

/*
 * POST to adduser.
 */
router.post('/', function(req, res) {
    var db = req.db;
    var collection = db.get('violations');
    collection.insert(req.body, function(err, result){
        res.send(
            (err === null) ? { msg: '' } : { msg: err }
        );
    });
});

/*
 * DELETE to deleteuser.
 */
router.delete('/:id', function(req, res) {
    var db = req.db;
    var collection = db.get('violations');
    var userToDelete = req.params.id;
    collection.remove({ '_id' : userToDelete }, function(err) {
        res.send((err === null) ? { msg: '' } : { msg:'error: ' + err });
    });
});

/*
 * PUT to deleteuser.
 */
router.put('/:id', function(req, res) {
    //TODO
    res.send('Got a PUT request at /violations');
});

module.exports = router;
