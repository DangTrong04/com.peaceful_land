import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SnackBarService } from '../../../core/services/snack-bar.service';
import { User } from '../../../dto/user';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-login-and-register',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './login-and-register.component.html',
  styleUrl: './login-and-register.component.css'
})
export class LoginAndRegisterComponent implements OnInit{
  @ViewChild('container') container!: ElementRef;
  today!: string;
  user!: User;
  packages = {
    'sale': [
      { days: 30, price: 99000 , img: 'nor-package'},
      { days: 90, price: 280000, img: 'nor-package' },
      { days: 180, price: 475000, img: 'nor-package' },
      { days: 360, price: 830000, img: 'nor-package' }
    ],
    'sale-pro': [
      { days: 30, price: 154000 , img: 'vip-package'},
      { days: 90, price: 440000 , img: 'vip-package'},
      { days: 180, price: 740000 , img: 'vip-package'},
      { days: 360, price: 1290000, img: 'vip-package' }
    ]
  };

  accountDetails: any[] = [
    {
      regularUserPosts: 5,
      agentPosts: 'Không giới hạn',
      vipAgentPosts: 'Không giới hạn',
      regularUserPostFrequency: '1 lần / ngày',
      agentPostFrequency: '5 lần / ngày',
      vipAgentPostFrequency: '10 lần / ngày',
      regularUserCategories: 'Chỉ nhà riêng',
      agentCategories: 'Tất cả',
      vipAgentCategories: 'Tất cả',
      regularUserApproval: 'Chờ duyệt',
      agentApproval: 'Chờ duyệt',
      vipAgentApproval: 'Xuất bản luôn, duyệt sau',
      regularUserMaxApprovalTime: '2 ngày',
      agentMaxApprovalTime: '1 ngày',
      vipAgentMaxApprovalTime: '1 ngày',
      regularUserMaxPostTime: '7 ngày',
      agentMaxPostTime: '10 ngày',
      vipAgentMaxPostTime: '14 ngày'
    }
  ];

  loginForm = new FormGroup({
    userId_login: new FormControl(''),
    password_login: new FormControl(''),
  })

  registerForm = new FormGroup({
    name: new FormControl(''),
    phone: new FormControl(''),
    email: new FormControl(''),
    password: new FormControl(''),
    birth_date: new FormControl('')
  })

  constructor(
    private snackbarService:SnackBarService,
    public authService:AuthService,
    private accountService:AccountService,
    private router:Router,
    private cdr: ChangeDetectorRef,
  ){}

  ngOnInit(): void {
    if(this.authService.getAuthStatus()){
      this.user = this.authService.getUserDetails();
    }

    // Today
    const now = new Date(); 
    const month = ('0' + (now.getMonth() + 1)).slice(-2); 
    const day = ('0' + now.getDate()).slice(-2); 
    this.today = `${now.getFullYear()}-${month}-${day}`;
  }

  handleLogin(event:Event){
    event.preventDefault();
    const userId_login = this.loginForm.get("userId_login")?.value?.trim() || ""; 
    const password_login = this.loginForm.get("password_login")?.value?.trim() || ""; 
    if (userId_login==="") {
      this.snackbarService.notifyWarningUser("Vui lòng nhập email hoặc mật khẩu");
    }else if (password_login===""){
      this.snackbarService.notifyWarningUser("Vui lòng nhập mật khẩu");
    }else{
      this.authService.login(userId_login,password_login)
        .subscribe({
          next: (response:any) => {
            if(response.account.role==3){
              this.router.navigate(["/admin"]);
            }else{
              window.location.reload();
            }
          },
          error: (response:any) => {
            this.snackbarService.notifyErrorUser(response.error.message)
          }
        })
    }
  }

  handleRegister(event:Event){
    event.preventDefault();
    const emailPattern= /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    const email = this.registerForm.get("email")?.value?.trim() || ""; 
    const password = this.registerForm.get("password")?.value?.trim() || ""; 
    const phone = this.registerForm.get("phone")?.value?.trim() || ""; 
    const name = this.registerForm.get("name")?.value?.trim() || ""; 
    const birth_date = this.registerForm.get("birth_date")?.value; 
    
    if(name == ""){
      this.snackbarService.notifyWarningUser("Vui lòng nhập họ tên");
    }else if(phone=="") {
      this.snackbarService.notifyWarningUser("Vui lòng nhập số điện thoại");
    }else if (email==="") {
      this.snackbarService.notifyWarningUser("Vui lòng nhập email");
    }else if(!emailPattern.test(email)){
      this.snackbarService.notifyWarningUser("Email không hợp lệ");
    }else if (password===""){
      this.snackbarService.notifyWarningUser("Vui lòng nhập mật khẩu");
    }else if (birth_date=="") {
      this.snackbarService.notifyWarningUser("Vui lòng chọn ngày sinh")
    }else{
      this.authService.register(email,password,phone,name,birth_date!)
        .subscribe({
          next: (response:any) => {
            this.snackbarService.notifySuccessUser("Đăng kí thành công");
            this.registerForm.get("email")?.setValue("");
            this.registerForm.get("password")?.setValue("");
            this.registerForm.get("phone")?.setValue("");
            this.registerForm.get("name")?.setValue("");
            this.registerForm.get("birth_date")?.setValue("");
            this.container.nativeElement.classList.remove("right-panel-active");
          },
          error: (response:any) => {
            this.snackbarService.notifyErrorUser(response.error.message)
          }
        })
    }
  }

  toLogin(){
    this.container.nativeElement.classList.remove("right-panel-active");
  }

  toRegister(){
    this.container.nativeElement.classList.add("right-panel-active");
  }

  logout(){
    this.authService.logout();
    window.location.reload();
  }

  purchaseRole(role: number,day:number){
    this.accountService.purchaseRole(this.user.id,role,day)
      .subscribe({
        next: (response:any) => {
          this.snackbarService.notifySuccessUser("Cập nhật gói thành công")
          this.authService.reloadUserAfterBuyRole(response)
          this.user = this.authService.getUserDetails();
          this.cdr.detectChanges()
        },
        error: (response:any) => {
          this.snackbarService.notifyErrorUser(response.error.message)
          console.log(response)
        }
      })
  }


}
