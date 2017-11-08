# [Git] New Line(CRLF, LF) 변환방법

Mac/Linux에서는 LF, Windows는 CRLF로 개행을 한다. 그렇다보니 다양한 os에서 개발 시, New Line에서 diff가 생기게 된다.
해결방법에는 다양한 방법이 있는데.. 일단 알아보자!

## git global 설정하기
[참고자료](https://git-scm.com/book/ko/v1/Git%EB%A7%9E%EC%B6%A4-Git-%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0)

### core.autocrlf
| 값 | 설명 | 추천os |
|--------|--------|--------|
| true | 자동으로 CRLF를 LF로 변환해주고, Checkout할 때에는 LF를 CRLF로 변환해주는 기능 활성화 | Windows |
| false | 위의 기능이 꺼진다. (CR도 커밋됨)| |
| input | 커밋할때에만 CRLF를 LF로 변환한다 | Linux/Mac|


```bash
# 윈도우에서는 true로 설정
$ git config --global core.autocrlf true
# 리눅스에서는 input으로 설정
$ git config --global core.autocrlf input
# 아마도 false가 디폴트 값일 것이다.
$ git config --global core.autocrlf false
# 설정값 확인해보기
$ git config --global --list | grep core

# 위 설정만 적용한다고 바로 적용되는게 아님. Git의 인덱스 캐쉬파일을 날린 후 위의 설정을 적용한 새로운 인덱스 파일을 만들어야 한다.
# Remove everything from the index
$ git rm --cached -r .

# Re-add all the deleted files to the index
# You should get lots of messages like: "warning: CRLF will be replaced by LF in <file>."
$ git diff --cached --name-only -z | xargs -0 git add

# 위의 커멘드를 완료하면 CRLF개행이었던 파일들이 모두 LF로 변경되는데 이를 바로 커밋하면 된다.
# Commit
$ git commit -m "Fix CRLF"
```


