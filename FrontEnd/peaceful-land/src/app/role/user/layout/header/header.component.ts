import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements AfterViewInit{
  isLoggedIn: boolean = false;
  isSaleOrUser: boolean = false;
  @ViewChild('nav_header') navBarElement!: ElementRef;
  @ViewChild('navbar_collapse') navbarCollapse!: ElementRef;

  constructor(
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private router:Router
  ){}

  ngAfterViewInit(): void {
    this.isLoggedIn = this.authService.getAuthStatus();
    this.isSaleOrUser = this.authService.getSaleOrUserStatus();
    this.cdr.detectChanges()
  }
  
  @HostListener('window:scroll', ['$event'])
  onScroll () {
    if (window.scrollY > 45) {
      this.navBarElement.nativeElement.classList.add('sticky-top');
    } else {
      this.navBarElement.nativeElement.classList.remove('sticky-top');
    }
  }

  toggleNavbarCollapse(){
    if(this.navbarCollapse.nativeElement.classList.contains("show")){
      this.navbarCollapse.nativeElement.classList.remove("show")
    }else{
      this.navbarCollapse.nativeElement.classList.add("show")
    }
  }

}
