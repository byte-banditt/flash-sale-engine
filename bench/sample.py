import subprocess,time,pathlib,json,datetime,sys
root=pathlib.Path(sys.argv[1])
while True:
    with (root/'live-stats.log').open('a') as f:
        f.write(datetime.datetime.now(datetime.timezone.utc).isoformat()+'\n')
        subprocess.run(['docker','stats','--no-stream','--format','{{json .}}'],stdout=f,stderr=f)
        subprocess.run(['free','-b'],stdout=f,stderr=f)
    time.sleep(5)
