window.helper = (function(win, doc){
    function renderElapsedString(elapsed, runningSince) {
        let totalElapsed = elapsed;
        if (runningSince) {
            totalElapsed += Date.now() - runningSince;
        }
        return millisecondsToHuman(totalElapsed);
    }

    function millisecondsToHuman(time) {
        const convertedTime = {
            ms : time % 1000,
            sec : Math.floor(time / 1000 % 60),
            min : Math.floor(time / 1000 / 60 % 60),
            hour : Math.floor(time / 1000 / 60 / 60)
        };

        return [
            pad(convertedTime.hour.toString(), 2),
            pad(convertedTime.min.toString(), 2),
            pad(convertedTime.sec.toString(), 2),
            pad(convertedTime.ms.toString(), 3),
        ].join(':');
    }

    function pad(numberString, size) {
        let padded = numberString;

        while (padded.length < size) {
            padded = `0${padded}`;
        }

        return padded;
    }

    return {
        renderElapsedString : renderElapsedString,
        millisecondsToHuman : millisecondsToHuman
    };
})(window, document);


$(document).ready(function(){
    var $list = $('#timers');

    function Timer(_elapsed, _runningSince) {
        var _default = {
            elapsed: 0,
            runningSince: null
        };

        this.id = Date.now();
        this.isRunning = false;
        this.intervalId = null;

        this.elapsed = _elapsed || _default.elapsed;
        this.runningSince = _runningSince || _default.runningSince;

        var $li =     $('<li class="row"  id="' + this.id + '">');
        var $wrap =   $('  <div class="col-4 offset-4 card p-3">');
        var $title =  $('    <h3 class="card-title text-center">Timer' + this.id + '</h3>');
        var $time =   $('    <p class="time card-text text-center">' + helper.renderElapsedString(this.elapsed, this.runningSince) + '</p>');
        var $start =   $('    <button class="start btn btn-primary">Start</button>');
        var $stop =    $('    <button class="stop btn btn-danger mt-1">Stop</button>');
        var $reset =    $('    <button class="reset btn btn-warning mt-1">Reset</button>');
        var $remove =    $('    <button class="remove btn btn-info mt-1">Delete</button>');

        // time의 텍스트를 수정하기 위한 변수 저장
        this.time = $time;

        // this가 button을 가리키지 않도록
        // 컨텍스트 고정
        const timer = this;

        $start.on('click', this.startTimer.bind(timer));
        $stop.on('click', this.stopTimer.bind(timer));
        $reset.on('click', this.resetTimer.bind(timer));
        $remove.on('click', this.removeTimer.bind(timer));

        $wrap.append($title).append($time).append($start).append($stop).append($reset).append($remove);
        $li.append($wrap);

        $list.append($li);
    }

    Timer.prototype.startTimer = function () {
        console.log(this);
        if (this.isRunning) return false;
        this.isRunning = true;

        const timer = this;

        if(timer.runningSince === null){
            timer.runningSince = Date.now();
        }
        timer.intervalId = setInterval(function(){
            timer.time.text(helper.renderElapsedString(timer.elapsed, timer.runningSince));
        }, 43)
    }

    Timer.prototype.stopTimer = function () {
        if (!this.isRunning) return false;
        this.isRunning = false;

        clearInterval(this.intervalId);
        const now = Date.now();
        let lastElapsed = now - this.runningSince;
        this.elapsed += lastElapsed;
        this.runningSince = null;
    }

    Timer.prototype.resetTimer = function() {
        this.elapsed = 0;

        if (this.isRunning) {
            this.runningSince = Date.now();
        } else {
            this.runningSince = null;
            this.time.text(helper.renderElapsedString(0, 0));
        }
    }

    Timer.prototype.removeTimer = function() {
        $('#' + this.id).remove();
    }

    $('#addTimer').on('click', function(e){
        // 여기 삭제하는 것도 넣기
        const timer = new Timer();
    });
});
