window.helper = (function(win, doc) {
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
    const $time = $('.time');
    const $start = $('.start');
    const $stop = $('.stop');

    let intervalId = null;
    let elapsed = 0;
    let runningSince = null;
    let isRunning = false;
    $time.text(helper.renderElapsedString(elapsed));

    function startTimer() {
        if (isRunning) return false;
        isRunning = true;

        if (runningSince === null) {
            runningSince = Date.now();
        }

        intervalId = setInterval(function(){
            $time.text(helper.renderElapsedString(elapsed, runningSince));
        }, 43);
    }

    function stopTimer() {
        if (!isRunning) return false;
        isRunning = false;

        clearInterval(intervalId);
        const now = Date.now();
        elapsed += now - runningSince;
        runningSince = null;
    }

    $start.on('click', startTimer);
    $stop.on('click', stopTimer);
});