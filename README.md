# VanityWallet v0.1.0
This java application will generate keys for a bitcoin wallet and search for a desired wallet address. If you have found a vanity address with this tool feel free to donate some bitcoin with your new address! 12nnswN71e9L**Qnasty**TrE4j1QWXguhTvuv

### Download: http://www.mediafire.com/file/byoe98n9qutltyf/file
	CRC32: B86CF101
	CRC64: 087408383381A9C1
    SHA256: F3F74E38E64A5A0213B386D7DCE59F7BC42F2FD84FA55122CAD3015E6EFE4083
    SHA1: F8A19921AD80262ED73F04E73190FB3002136801

1. When getting started you should put the jar in its own folder and run using the terminal or command line. 
1. To add your own name or names to be found locate the file "names.dat" located in the "data" folder.
    * When searching for more than one name use commas to seperate them like: example,ex4mpl3,test
    * If only searching for one name just put that name in the file.
	* Names can be search by addresses beginning with or ending with the name.
	* Names can also be searched by matching case.
	* Matching = m | Starts with = s | Ends with = e
	* Valids uses: name:m | test:s | example:e | also:ms(match and starts) | last:me(match and ends)
1. When the application discovers a valid wallet address it will save the Private Key, Public Key, Wallet Address in a file in the found directory.

## **Dependencies**

* BouncyCastle: https://www.bouncycastle.org/latest_releases.html

* BitcoinJ: https://github.com/bitcoinj/bitcoinj

#### Donate Btc: 12nnswN71e9L*Qnasty*TrE4j1QWXguhTvuv
