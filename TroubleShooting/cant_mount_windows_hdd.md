# Linux에서 Windows 하드디스크 인식이 되지 않는 경우 대처법

리눅스와 윈도우즈를 함께 사용하는 멀티부팅 환경에서, 잘 인식되던 하드디스크가 리눅스에서 마운트 되지 않을때가 있다.
윈도우즈에서 사용하게되면 Windows 파일시스템으로 셋팅되어 인식되지 않는 것이다.

일단 fdisk로 디스크상태를 확인하자
```bash
$ sudo fdisk -l
Disk /dev/sda: 477 GiB, 512110190592 bytes, 1000215216 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disklabel type: gpt
Disk identifier: A37317BF-FB85-47D2-935B-4EA485CF8BD4

Device         Start        End   Sectors   Size Type
/dev/sda1       2048     923647    921600   450M Windows recovery environment
/dev/sda2     923648    1126399    202752    99M EFI System
/dev/sda3    1126400    1159167     32768    16M Microsoft reserved
/dev/sda4    1159168  730855423 729696256   348G Microsoft basic data
/dev/sda5  999290880 1000212479    921600   450M Windows recovery environment
/dev/sda6  730855424  738854911   7999488   3.8G Linux swap
/dev/sda7  738854912  999290879 260435968 124.2G Linux filesystem

Partition table entries are not in disk order.


Disk /dev/sdb: 1.8 TiB, 2000398934016 bytes, 3907029168 sectors
Units: sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 4096 bytes
I/O size (minimum/optimal): 4096 bytes / 4096 bytes
Disklabel type: gpt
Disk identifier: 13ACEFF1-34F6-45F8-A142-3D807C6D0A18

Device     Start        End    Sectors  Size Type
/dev/sdb1   2048      34815      32768   16M Microsoft reserved
/dev/sdb2  34816 3907026943 3906992128  1.8T Microsoft basic data
```
나는 /dev/sdb2 를 하드디스크로서 리눅스, 윈도우에서 모두 사용하고 있었다.
하지만 지금은 위와 같이 Microsoft basic data 타입이 되어있는 상태!

이를 마운트하려고 해보면 아래와 같이 에러 메세지가 나타난다.
```bash
$ sudo mount /dev/sdb2 /media/yenarue/HDD_2TB
The disk contains an unclean file system (0, 0).
Metadata kept in Windows cache, refused to mount.
Failed to mount '/dev/sdb2': Operation not permitted
The NTFS partition is in an unsafe state. Please resume and shutdown
Windows fully (no hibernation or fast restarting), or mount the volume
read-only with the 'ro' mount option.
```

대망의 해결법은 바로, ```ntfsfix``` 명령어로 NTFS 포맷으로 변경하는 것!
```bash
$ sudo ntfsfix /dev/sdb2
Mounting volume... The disk contains an unclean file system (0, 0).
Metadata kept in Windows cache, refused to mount.
FAILED
Attempting to correct errors... 
Processing $MFT and $MFTMirr...
Reading $MFT... OK
Reading $MFTMirr... OK
Comparing $MFTMirr to $MFT... OK
Processing of $MFT and $MFTMirr completed successfully.
Setting required flags on partition... OK
Going to empty the journal ($LogFile)... OK
Checking the alternate boot sector... OK
NTFS volume version is 3.1.
NTFS partition /dev/sdb2 was processed successfully.
```
위와같이 나타나면 성공이다 :-)
윈도우즈로 부팅해서 해당 하드를 사용하게되면 윈도우즈 파일시스템으로 변경되므로 번거롭지만 리눅스로 전환할 때 마다 계속 해줘야하는 작업이다ㅠㅠ


