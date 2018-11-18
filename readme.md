[![GitHub issues](https://img.shields.io/github/issues/antop-dev/goos-code-example.svg)](https://github.com/antop-dev/goos-code-example/issues)
[![GitHub issues](https://img.shields.io/github/issues/antop-dev/goos-code-example.svg)](https://github.com/antop-dev/goos-code-example/issues)
[![GitHub stars](https://img.shields.io/github/stars/antop-dev/goos-code-example.svg)](https://github.com/antop-dev/goos-code-example/stargazers)
[![GitHub license](https://img.shields.io/github/license/antop-dev/goos-code-example.svg)](https://github.com/antop-dev/goos-code-example/blob/master/license.txt)
[![Twitter](https://img.shields.io/twitter/url/https/github.com/antop-dev/goos-code-example.svg?style=social)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fantop-dev%2Fgoos-code-example)

## Source code for the book, "Growing Object-Oriented Software, Guided by Tests"
forked from [sf105/goos-code](https://github.com/sf105/goos-code)

오래된 소스 코드 개선

1. 이클립스 프로젝트 파일 제거
2. 메이븐 프로젝트 구조로 로 변경
3. jdk 1.6(or 1.5?)에서 1.8로 변경
4. End-to-End 테스트 에러 수정
5. 기타 경고 수정

### Openfire 환경 설정

* 사용자 계정 추가 (p107)

	![Imgur](https://i.imgur.com/geCEiT0.png)

* src/test/java/endtoend/FakeAuctionServer.java
	```java
	public class FakeAuctionServer {
		// 호스트명 변경
    	public static final String XMPP_HOSTNAME = "localhost";
	}
	```
