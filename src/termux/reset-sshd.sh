pkill sshd

echo ""

if ! pgrep "sshd" >/dev/null ; then echo "[Starting OpenSSH Daemon...]" && sshd && echo "[OK.]"; else echo "[The OpenSSH Daemon is running.]"; fi

{ echo "Connect to Termux: ";  whoami; echo "@"; ifconfig arc0 | awk '/inet /{print $2}'; echo ":8022"; } | sed ':a;N;s/\n//;ba'

echo ""
